package org.homanhquan.authservice.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.homanhquan.authservice.enums.EmailType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage implements Serializable {
    private String to;
    private String username;
    private String subject;
    private EmailType type;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
}
