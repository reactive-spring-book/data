package rsb.data.r2dbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

	@Id
	private Integer id;

	private String email;

	Customer(String e) {
		this.email = e;
	}

}
