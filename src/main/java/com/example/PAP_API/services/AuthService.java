//package com.example.PAP_API.services;
//
//import com.example.PAP_API.dto.AuthRequest;
//import com.example.PAP_API.dto.AuthResponse;
//import com.example.PAP_API.model.TheUser;
//import com.example.PAP_API.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class AuthService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public ResponseEntity<AuthResponse> login(AuthRequest authRequest) {
//        Optional<TheUser> userOptional = userRepository.findByTheUsername(authRequest.getUsername());
//        System.out.println("data base "+userOptional);
//
//        if (userOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new AuthResponse("No user in this name!",  null));
//        }
//
//        TheUser user = userOptional.get();
//
//        // Simple string comparison (No Hashing)
//        if (!authRequest.getPassword().equals(user.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new AuthResponse("Invalid Credentials!", null));
//        }
//// ✅ Return user details
//        return ResponseEntity.ok(new AuthResponse("Login successful", user.getUsername()));
//    }
//}
