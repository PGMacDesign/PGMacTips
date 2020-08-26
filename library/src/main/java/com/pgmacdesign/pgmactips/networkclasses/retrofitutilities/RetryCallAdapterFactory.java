package com.pgmacdesign.pgmactips.networkclasses.retrofitutilities;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pgmacdesign.pgmactips.utilities.L;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Retry Call Adapter Factory to be used in the {@link RetrofitClient} class
 * Code pulled from MORRIS at this link: https://androidwave.com/retrying-request-with-retrofit-android/
 */
public class RetryCallAdapterFactory extends CallAdapter.Factory {
	
	//region Interface
	
	
	/**
	 * Retry Interface. Defaults to 3 attempts
	 */
	@Documented
	@Target(METHOD)
	@Retention(RUNTIME)
	public @interface Retry {
		int max() default 3;
	}
	
	//endregion
	/**
	 * Customizable number of retry attempts set
	 */
	private int retryAttempts;
	
	//region Constructor
	
	private RetryCallAdapterFactory(){
		this.retryAttempts = 3;
	}
	
	private RetryCallAdapterFactory(int retryAttempts){
		this.retryAttempts = retryAttempts;
	}
	
	//endregion
	
	/**
	 * Static Instance Generator
	 * @return
	 */
	public static RetryCallAdapterFactory create() {
		return new RetryCallAdapterFactory();
	}
	
	/**
	 * Static Instance Generator
	 * @return
	 */
	public static RetryCallAdapterFactory create(@IntRange (from = 0) int retryCount) {
		return new RetryCallAdapterFactory(retryCount);
	}
	
	/**
	 * Constructor for the Call Adapter
	 * @param returnType
	 * @param annotations
	 * @param retrofit
	 * @return
	 */
	@Nullable
	@Override
	public CallAdapter<?, ?> get(@NonNull Type returnType, @NonNull Annotation[] annotations,
	                             @NonNull Retrofit retrofit) {
		/**
		 * You can setup a default max retry count for all connections.
		 */
		int itShouldRetry = 0;
		final Retry retry = getRetry(annotations);
		if (retry != null) {
			itShouldRetry = retry.max();
		} else {
			itShouldRetry = this.retryAttempts;
		}
		return new RetryCallAdapter<>(
				retrofit.nextCallAdapter(this, returnType, annotations), itShouldRetry
		);
	}
	
	/**
	 * Simple getter method to return the interface defined above
	 * @param annotations
	 * @return
	 */
	private Retry getRetry(@NonNull Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Retry) {
				return (Retry) annotation;
			}
		}
		return null;
	}
	
	/**
	 * The Call Adapter
	 * @param <R>
	 * @param <T>
	 */
	static final class RetryCallAdapter<R, T> implements CallAdapter<R, T> {
		
		private final CallAdapter<R, T> delegated;
		private final int maxRetries;
		public RetryCallAdapter(CallAdapter<R, T> delegated, int maxRetries) {
			this.delegated = delegated;
			this.maxRetries = maxRetries;
		}
		@Override
		public Type responseType() {
			return this.delegated.responseType();
		}
		@Override
		public T adapt(final Call<R> call) {
			return this.delegated.adapt(this.maxRetries > 0 ? new RetryingCall<>(call, this.maxRetries) : call);
		}
	}
	
	/**
	 * Method for retrying the call
	 * @param <R>
	 */
	static final class RetryingCall<R> implements Call<R> {
		private final Call<R> delegated;
		private final int maxRetries;
		public RetryingCall(Call<R> delegated, int maxRetries) {
			this.delegated = delegated;
			this.maxRetries = maxRetries;
		}
		@Override
		public Response<R> execute() throws IOException {
			return this.delegated.execute();
		}
		@Override
		public void enqueue(@NonNull Callback<R> callback) {
			this.delegated.enqueue(new RetryCallback<>(this.delegated, callback, this.maxRetries));
		}
		@Override
		public boolean isExecuted() {
			return this.delegated.isExecuted();
		}
		@Override
		public void cancel() {
			this.delegated.cancel();
		}
		@Override
		public boolean isCanceled() {
			return this.delegated.isCanceled();
		}
		@Override
		public Call<R> clone() {
			return new RetryingCall<>(this.delegated.clone(), this.maxRetries);
		}
		@Override
		public Request request() {
			return this.delegated.request();
		}
		
		@Override
		public Timeout timeout() {
			return this.delegated.timeout();
		}
	}
	
	/**
	 * Retry Callback
	 * @param <T>
	 */
	static final class RetryCallback<T> implements Callback<T> {
		private final Call<T> call;
		private final Callback<T> callback;
		private final int maxRetries;
		
		public RetryCallback(Call<T> call, Callback<T> callback, int maxRetries) {
			this.call = call;
			this.callback = callback;
			this.maxRetries = maxRetries;
		}
		
		private final AtomicInteger retryCount = new AtomicInteger(0);
		
		@Override
		public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
			if(this.maxRetries <= 0){
				this.callback.onResponse(call, response);
				return;
			}
			if (!response.isSuccessful() && (this.retryCount.incrementAndGet() <= this.maxRetries)) {
				retryCall();
			} else {
				this.callback.onResponse(call, response);
			}
		}
		@Override
		public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
			int x = this.retryCount.incrementAndGet();
			if(this.maxRetries <= 0){
				this.callback.onFailure(call, t);
				return;
			}
			if (x <= this.maxRetries) {
				retryCall();
			} else if (this.maxRetries > 0) {
				this.callback.onFailure(call,
						new TimeoutException(String.format("Call failed after %s attempts.", this.maxRetries)));
			} else {
				this.callback.onFailure(call, t);
			}
		}
		private void retryCall() {
			L.m("" + this.retryCount.get() + "/" + this.maxRetries + " " + " Retrying...");
			this.call.clone().enqueue(this);
		}
	}
	

}
