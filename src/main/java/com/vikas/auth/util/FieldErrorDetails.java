package com.vikas.auth.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class      : FieldErrorDetails
 * Description: [Add brief description here]
 * Author     : Vikas Yadav
 * Created On : Mar 2, 2026
 * Version    : 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDetails {
    private String field;
    private String error;
}