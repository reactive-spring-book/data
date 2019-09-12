package rsb.data.r2dbc.basics;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.connectionfactory.ConnectionFactoryUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
class ConnectionManager {

	private final ConnectionFactory connectionFactory;

	public <T> Flux<T> inConnection(Function<Connection, Flux<T>> action) {
		Function<ConnectionCloseHolder, Publisher<?>> close = ConnectionCloseHolder::close;
		return Flux.usingWhen(connection(), holder -> action.apply(holder.connection),
				close, close, close);
	}

	private Mono<ConnectionCloseHolder> connection() {
		return ConnectionFactoryUtils//
				.getConnection(connectionFactory)//
				.map(c -> new ConnectionCloseHolder(c, this::closeConnection));
	}

	@RequiredArgsConstructor
	static class ConnectionCloseHolder extends AtomicBoolean {

		private static final long serialVersionUID = -8994138383301201380L;

		final Connection connection;

		final Function<Connection, Publisher<Void>> closeFunction;

		Mono<Void> close() {

			return Mono.defer(() -> {

				if (compareAndSet(false, true)) {
					return Mono.from(this.closeFunction.apply(this.connection));
				}

				return Mono.empty();
			});
		}

	}

	private Publisher<Void> closeConnection(Connection connection) {
		return ConnectionFactoryUtils.currentConnectionFactory(this.connectionFactory)
				.then()
				.onErrorResume(Exception.class, e -> Mono.from(connection.close()));
	}

}
