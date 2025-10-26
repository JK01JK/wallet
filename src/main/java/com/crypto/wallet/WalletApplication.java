package com.crypto.wallet;

import com.crypto.wallet.model.User;
import com.crypto.wallet.model.Wallet;
import com.crypto.wallet.repository.UserRepository;
import com.crypto.wallet.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserRepository userRepo, WalletRepository walletRepo) {
        return args -> {
            User user = new User("heyho@example.com");
            userRepo.save(user);

            Wallet wallet = new Wallet(user);
            walletRepo.save(wallet);

            user.setWallet(wallet);
            userRepo.save(user);

            System.out.println("✅ User saved: " + user.getEmail());
            System.out.println("✅ Wallet created with ID: " + wallet.getId());
        };
    }
}
