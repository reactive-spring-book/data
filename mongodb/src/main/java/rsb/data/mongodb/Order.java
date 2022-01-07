package rsb.data.mongodb;

import org.springframework.data.annotation.Id;

record Order(@Id String id, String productId) {
}