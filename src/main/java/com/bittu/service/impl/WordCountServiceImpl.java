package com.bittu.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bittu.model.Word;
import com.bittu.service.WordCountService;

import scala.Tuple2;

@Service
public class WordCountServiceImpl implements WordCountService {

	@Autowired
	private SparkSession sparkSession;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Word> getCount() throws IOException {

		JavaRDD<Row> lines = sparkSession.read()
				.text("D:\\workspace\\SparkSpringBootDemo\\src\\main\\resources\\sample.txt").javaRDD();

		JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(s.getString(0).split(" ")).iterator());

		JavaPairRDD<String, Integer> countData = words.mapToPair(t -> new Tuple2(t, 1))
				.reduceByKey((x, y) -> (int) x + (int) y);

		System.out.println(countData);

		List<Word> wordList = new ArrayList<>();

		for (Tuple2<String, Integer> test : countData.collect()) {
			wordList.add(new Word(test._1, test._2));
		}

		return wordList;
	}

}
