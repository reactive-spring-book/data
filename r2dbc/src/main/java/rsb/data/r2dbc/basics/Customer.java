package rsb.data.r2dbc.basics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {

	private Integer id;

	private String email;

	Customer(String e) {
		this.email = e;
	}

}
