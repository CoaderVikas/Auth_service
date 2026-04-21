package com.vikas.auth.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Class      : PaginatedUserResponse
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Apr 21, 2026
 * Version    : 1.0
 */
@Data
@Builder
public class PaginatedUserResponse {
	private List<AdminUserResponse> users;
	private long totalElements;
	private int totalPages;
	private int currentPage;
	private boolean isLast;
}
