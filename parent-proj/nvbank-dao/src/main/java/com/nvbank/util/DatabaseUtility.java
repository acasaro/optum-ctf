package com.nvbank.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class DatabaseUtility
{
	//private static BasicDataSource dataSource;
	private static DataSource dataSource;
	private static final Logger logger = LoggerFactory.getLogger(DatabaseUtility.class);

	//public static BasicDataSource getDataSource()
	public static DataSource getDataSource()
	{
		if (dataSource == null) 
		{
			try
			{
				try
				{
					Context initContext = new InitialContext();
					Context envContext = (Context) initContext.lookup("java:comp/env");
			        dataSource = (DataSource) envContext.lookup("jdbc/nvbank");
				}
				catch (javax.naming.NoInitialContextException nice)
				{
					// this exception gets thrown when running inside spring-boot's tomcat server
					// lets build a datasource manually
					BasicDataSource ds = new BasicDataSource();
					ds.setUrl("jdbc:sqlserver://sql.team-c.nvisiumlabs.com:49775;databaseName=nvbank");
					
					ds.setUsername("sa");
					ds.setPassword("Summer2016");
		
					ds.setMinIdle(5);
					ds.setMaxIdle(10);
					ds.setMaxOpenPreparedStatements(100);
		
					dataSource = (DataSource) ds;
				}
			}
			catch (Exception e)
			{
				logger.error(e.toString());
			}
		}
		
		return dataSource;
	}
}
