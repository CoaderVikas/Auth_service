package com.vikas.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : UserRegisteredEvent
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 7, 2026
 * Version    : 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisteredEvent {
	private String email;
	private String fullName;
}
