package rsb.data.r2dbc;

import org.springframework.data.annotation.Id;

public record Customer(@Id Integer id, String email) {
}
