package org.ats.security;

import org.ats.entities.car.Car;
import org.ats.entities.car.CarProducer;
import org.ats.entities.user.Account;
import org.ats.entities.user.Customer;
import org.ats.repository.account.AccountRepository;
import org.ats.repository.car.CarProducerRepository;
import org.ats.repository.car.CarRepository;
import org.ats.repository.customer.CustomerRepository;
import org.ats.security.service.UserDetailsServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final CustomSuccessHandler successHandler;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, CustomSuccessHandler successHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/auth/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/v1/customer/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/v1/auth/login")
                        .usernameParameter("accountName")
                        .passwordParameter("password")
                        .failureUrl("/v1/auth/login?error=true")
                        .successHandler(successHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/v1/auth/logout")
                        .logoutSuccessUrl("/v1/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        http.authenticationProvider(authenticationProvider());
        return http.build();
    }

    @Bean
    public CommandLineRunner initData(AccountRepository accountRepository,
                                      CarProducerRepository producerRepository,
                                      CarRepository carRepository,
                                      CustomerRepository customerRepository) {
        return args -> {
            Account admin = accountRepository.findAccountByAccountNameIgnoreCase("admin")
                    .orElseGet(() -> Account.builder()
                            .accountName("admin")
                            .email("admin@fucar.local")
                            .role("ADMIN")
                            .build());
            admin.setRole("ADMIN");
            admin.setPassword(passwordEncoder().encode("admin"));
            accountRepository.save(admin);

//            if (!accountRepository.existsAccountByEmail("customer1@gmail.com")) {
//                Account account = Account.builder()
//                        .accountName("Customer 1")
//                        .email("customer1@gmail.com")
//                        .password(passwordEncoder().encode("customer123"))
//                        .role("CUSTOMER")
//                        .build();
//                Customer customer = Customer.builder()
//                        .fullName("Nguyễn Văn An")
//                        .mobile("0901234567")
//                        .birthday(LocalDate.of(1995, 5, 15))
//                        .identityCard("079095000001")
//                        .licenceNumber("790000000001")
//                        .licenceDate(LocalDate.of(2020, 6, 1))
//                        .account(account)
//                        .build();
//                account.setCustomer(customer);
//                customerRepository.save(customer);
//            }
//            if (!producerRepository.existsCarProducerByProducerName("BMW Group")) {
//                producerRepository.save(CarProducer.builder()
//                        .producerName("BMW Group")
//                        .address("Munich")
//                        .country("Germany")
//                        .build());
//            }
//            if (!carRepository.existsCarByCarNameIgnoreCase("BMW X3")) {
//                CarProducer producer = producerRepository.findCarProducerByProducerName("BMW Group").orElseThrow();
//                carRepository.save(Car.builder()
//                        .carName("BMW X3")
//                        .carModelYear(2026)
//                        .color("Red")
//                        .capacity(7)
//                        .description("Family SUV")
//                        .importDate(LocalDate.now())
//                        .rentPrice(new BigDecimal("1500000"))
//                        .status("AVAILABLE")
//                        .carProducer(producer)
//                        .build());
//            }
        };
    }
}
