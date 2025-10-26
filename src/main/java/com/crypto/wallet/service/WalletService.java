package com.crypto.wallet.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crypto.wallet.dto.AssetView;
import com.crypto.wallet.dto.WalletDTO;
import com.crypto.wallet.dto.WalletResponse;
import com.crypto.wallet.model.User;
import com.crypto.wallet.model.Wallet;
import com.crypto.wallet.repository.UserRepository;
import com.crypto.wallet.repository.WalletRepository;

@Service
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public WalletService(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletDTO createWallet(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("A wallet with this email already exists.");
        }

        User user = new User(email);
        userRepository.save(user);

        Wallet wallet = new Wallet(user);
        walletRepository.save(wallet);

        user.setWallet(wallet);
        userRepository.save(user);

        return new WalletDTO(wallet.getId(), user.getEmail(), wallet.getCreatedAt());
    }
    
    
    @Transactional(readOnly = true)
    public WalletDTO getWalletById(Long id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found with id: " + id));

        User user = wallet.getUser();

        return new WalletDTO(wallet.getId(), user.getEmail(), wallet.getCreatedAt());
    }
    
    @Transactional(readOnly = true)
    public WalletResponse getWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

        List<AssetView> items = wallet.getAssets().stream()
                .map(a -> new AssetView(
                        a.getSymbol(),
                        a.getQuantity(),
                        a.getPrice(),
                        a.getValue(),
                        a.getLastUpdated()
                ))
                .toList();

        BigDecimal total = items.stream()
                .map(AssetView::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new WalletResponse(
                wallet.getId(),
                wallet.getUser().getEmail(),
                wallet.getCreatedAt(),
                total,
                items
        );
    }

    
}
