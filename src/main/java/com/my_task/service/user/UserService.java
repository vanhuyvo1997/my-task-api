package com.my_task.service.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.my_task.model.User;
import com.my_task.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;

	public UserResponse create(UserRequest userRequest) {
		if(userRepository.findByEmail(userRequest.email()).isPresent()) {
			throw new UserAlreadyExistedException(userRequest.email() + " is existed.");
		}
		
		User newUser = userRequest.toUser();
		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		var createdUser = userRepository.save(newUser);
		return UserResponse.form(createdUser);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
		
	}
	
	
	
}
