package rsb.data.mongodb;

import org.springframework.data.annotation.Id;

record Customer(@Id String id, String name) {
}