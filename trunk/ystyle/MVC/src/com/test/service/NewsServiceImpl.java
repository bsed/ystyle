package com.test.service;

import org.love.Annotation.Autowired;
import org.love.Annotation.Service;
import org.love.Annotation.SingleTon;
import org.love.Annotation.Transactional;

import com.test.dao.NewsDAO;
import com.test.dao.NewsDaoImpl;
import com.test.vo.News;

@Service
@SingleTon
public class NewsServiceImpl implements NewsService {

	@Autowired(iocClass = NewsDaoImpl.class)
	private NewsDAO newsDAO;

	public void setNewsDAO(NewsDAO newsDAO) {
		this.newsDAO = newsDAO;
	}

	@Transactional
	public void save(News news) {
      newsDAO.save(news);
	}

}
