package com.github.djroush.scrabbleservice.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.djroush.scrabbleservice.repository.DictionaryRepository;

@Configuration
public class DictionaryRepositoryConfig {

	@Bean @Qualifier("length2")
	public DictionaryRepository length2() {
		return new DictionaryRepository(2);
	}
	@Bean @Qualifier("length3")
	public DictionaryRepository length3() {
		return new DictionaryRepository(3);
	}
	@Bean @Qualifier("length4")
	public DictionaryRepository length4() {
		return new DictionaryRepository(4);
	}
	@Bean @Qualifier("length5")
	public DictionaryRepository length5() {
		return new DictionaryRepository(5);
	}
	@Bean @Qualifier("length6")
	public DictionaryRepository length6() {
		return new DictionaryRepository(6);
	}
	@Bean @Qualifier("length7")
	public DictionaryRepository length7() {
		return new DictionaryRepository(7);
	}
	@Bean @Qualifier("length8")
	public DictionaryRepository length8() {
		return new DictionaryRepository(8);
	}
	@Bean @Qualifier("length9")
	public DictionaryRepository length9() {
		return new DictionaryRepository(9);
	}
	@Bean @Qualifier("length10")
	public DictionaryRepository length10() {
		return new DictionaryRepository(10);
	}
	@Bean @Qualifier("length11")
	public DictionaryRepository length11() {
		return new DictionaryRepository(11);
	}
	@Bean @Qualifier("length12")
	public DictionaryRepository length12() {
		return new DictionaryRepository(12);
	}
	@Bean @Qualifier("length13")
	public DictionaryRepository length13() {
		return new DictionaryRepository(13);
	}
	@Bean @Qualifier("length14")
	public DictionaryRepository length14() {
		return new DictionaryRepository(14);
	}
	@Bean @Qualifier("length15")
	public DictionaryRepository length15() {
		return new DictionaryRepository(15);
	}
}
