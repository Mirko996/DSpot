package com.SimpleSoft.dspot.DSpot.service.impl;

import com.SimpleSoft.dspot.DSpot.domain.*;
import com.SimpleSoft.dspot.DSpot.dto.order.CreateOrderRequest;
import com.SimpleSoft.dspot.DSpot.dto.order.OrderItemResponse;
import com.SimpleSoft.dspot.DSpot.dto.order.OrderResponse;
import com.SimpleSoft.dspot.DSpot.enums.OrderStatus;
import com.SimpleSoft.dspot.DSpot.exception.ServiceException;
import com.SimpleSoft.dspot.DSpot.repository.DistributorRepository;
import com.SimpleSoft.dspot.DSpot.repository.OrderRepository;
import com.SimpleSoft.dspot.DSpot.repository.ProductRepository;
import com.SimpleSoft.dspot.DSpot.repository.UserRepository;
import com.SimpleSoft.dspot.DSpot.security.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements com.SimpleSoft.dspot.DSpot.service.OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DistributorRepository distributorRepository;

    @Transactional
    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        User storeUser = userRepository.findById(request.getStoreUserId())
                .orElseThrow(() -> new ServiceException("Store user not found"));

        User salesRep = null;
        if (request.getSalesRepId() != null) {
            salesRep = userRepository.findById(request.getSalesRepId())
                    .orElseThrow(() -> new ServiceException("Sales rep not found"));
        }

        Order order = Order.builder()
                .distributor(distributor)
                .storeUser(storeUser)
                .salesRep(salesRep)
                .status(OrderStatus.PENDING)
                .isLocked(false)
                .build();

        List<OrderItem> orderItems = request.getProducts().stream().map(itemReq -> {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ServiceException("Product not found: " + itemReq.getProductId()));

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        orderRepository.save(order);

        return toResponse(order);
    }

    public Page<OrderResponse> getOrdersForCurrentUser(int page, int size) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        String userEmail = AuthUtils.getCurrentUserEmail();
        String role = AuthUtils.getCurrentUserRole();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ServiceException("Authenticated user not found"));

        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;
        switch (role) {
            case "RETAIL_STORE":
                orders = orderRepository.findAllByStoreUserAndDistributor(user, distributor, pageable);
                break;
            case "SALES_REP":
                orders = orderRepository.findAllBySalesRepAndDistributor(user, distributor, pageable);
                break;
            case "ADMIN":
                orders = orderRepository.findAllByDistributor(distributor, pageable);
                break;
            default:
                throw new ServiceException("Access denied for role: " + role);
        }
        return orders.map(this::toResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        String role = AuthUtils.getCurrentUserRole();

        Order order = orderRepository.findByIdAndDistributor(orderId, distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found")));

        if (order == null) {
            throw new ServiceException("Order not found");
        }

        // Enforce role-based transitions (basic example)
        switch (newStatus) {
            case PACKED:
                if (!"LOADER".equals(role) && !"ADMIN".equals(role))
                    throw new ServiceException("Only loader or admin can mark as PACKED");
                break;
            case ON_THE_WAY:
                if (!"DRIVER".equals(role) && !"ADMIN".equals(role))
                    throw new ServiceException("Only driver or admin can mark as ON_THE_WAY");
                break;
            case DELIVERED:
                if (!"DRIVER".equals(role) && !"ADMIN".equals(role))
                    throw new ServiceException("Only driver or admin can mark as DELIVERED");
                break;
            case CANCELLED:
                if (!"ADMIN".equals(role))
                    throw new ServiceException("Only admin can cancel orders");
                break;
            default:
                throw new ServiceException("Invalid or unauthorized transition");
        }

        if (order.isLocked())
            throw new ServiceException("Order is locked and cannot be changed");

        order.setStatus(newStatus);
        orderRepository.save(order);
        return toResponse(order);
    }

    public Page<OrderResponse> getOrdersFiltered(OrderStatus status, int page, int size) {
        Long distributorId = AuthUtils.getCurrentDistributorId();
        String email = AuthUtils.getCurrentUserEmail();
        String role = AuthUtils.getCurrentUserRole();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceException("User not found"));

        Distributor distributor = distributorRepository.findById(distributorId)
                .orElseThrow(() -> new ServiceException("Distributor not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        User storeUser = null;
        User salesRep = null;

        switch (role) {
            case "RETAIL_STORE" -> storeUser = user;
            case "SALES_REP" -> salesRep = user;
            case "ADMIN" -> {} // leave both null to get all
            default -> throw new ServiceException("Access denied");
        }

        Page<Order> orders = orderRepository.searchOrders(distributor, storeUser, salesRep, status, pageable);

        return orders.map(this::toResponse);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.isLocked(),
                order.getStoreUser().getName(),
                order.getSalesRep() != null ? order.getSalesRep().getName() : null,
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
