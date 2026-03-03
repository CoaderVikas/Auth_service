package com.vikas.auth.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.vikas.auth.repository.UserRepository;

/**
 * Class      : CustomUserDetailsService
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 3, 2026
 * Version    : 1.0
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repository; 

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Aapki UserEntity ko uthakar UserDetails mein wrap karna
		return repository.findByUsername(username)
						.map(user -> 
							User.builder()
								.username(user.getUsername())
								.password(user.getPassword())
						.roles(user.getRole()).build())
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}