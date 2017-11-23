package net.rubyeye.xmemcached.example;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.KeyIterator;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple example for xmemcached
 * 
 * @author boyan
 * 
 */
public class SimpleExample {

	private static final Logger log = LoggerFactory
			.getLogger(SimpleExample.class);

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Useage:java SimpleExample [servers]");
			System.exit(1);
		}
		MemcachedClient memcachedClient = getMemcachedClient(args[0]);
		if (memcachedClient == null) {
			throw new NullPointerException(
					"Null MemcachedClient,please check memcached has been started");
		}
		try {
			// add a,b,c
			System.out.println("Add a,b,c");
			memcachedClient.set("a", 0, "Hello,xmemcached");
			memcachedClient.set("b", 0, "Hello,xmemcached");
			memcachedClient.set("c", 0, "Hello,xmemcached");
			// get a
			String value = memcachedClient.get("a");
			System.out.println("get a=" + value);
			System.out.println("delete a");
			// delete a
			memcachedClient.delete("a");
			// reget a
			value = memcachedClient.get("a");
			System.out.println("after delete,a=" + value);

			System.out.println("Iterate all keys...");
			// iterate all keys
			KeyIterator it = memcachedClient
					.getKeyIterator(AddrUtil.getOneAddress(args[0]));
			while (it.hasNext()) {
				System.out.println(it.next());
			}
			System.out.println(memcachedClient.touch("b", 1000));

		} catch (MemcachedException e) {
			log.error("MemcachedClient operation fail", e);
		} catch (TimeoutException e) {
			log.error("MemcachedClient operation timeout", e);
		} catch (InterruptedException e) {
			// ignore
			log.info(e.getMessage());
		}
		try {
			memcachedClient.shutdown();
		} catch (Exception e) {
			log.error("Shutdown MemcachedClient fail", e);
		}
	}

	public static MemcachedClient getMemcachedClient(String servers) {
		try {
			// use text protocol by default
			MemcachedClientBuilder builder = new XMemcachedClientBuilder(
					AddrUtil.getAddresses(servers));
			return builder.build();
		} catch (IOException e) {
			log.error("Create MemcachedClient fail", e);
		}
		return null;
	}
}
