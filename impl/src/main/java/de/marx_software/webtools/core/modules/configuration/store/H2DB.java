
package de.marx_software.webtools.core.modules.configuration.store;

/*-
 * #%L
 * webtools-configuration
 * %%
 * Copyright (C) 2016 - 2018 Thorsten Marx
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author marx
 */
public class H2DB implements DB {

	private static final Logger LOGGER = LoggerFactory.getLogger(H2DB.class);

	private final File path;

	private JdbcConnectionPool pool;

	public H2DB(final File path) {
		this.path = path;
	}

	public void open() {
		try {
			File dataDir = new File(path, "/registry");
			if (!dataDir.exists()) {
				dataDir.mkdirs();
			}
			Class.forName("org.h2.Driver");
			pool = JdbcConnectionPool.create("jdbc:h2:" + path.getAbsolutePath() + "/registry/store.db", "sa", "sa");
			init();

		} catch (ClassNotFoundException ex) {
			LOGGER.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	private void init() {
		try (Connection connection = pool.getConnection();
				Statement st = connection.createStatement();) {

			st.execute("CREATE TABLE IF NOT EXISTS configuration (db_namespace VARCHAR(255), db_key VARCHAR(255), db_content CLOB, PRIMARY KEY(db_namespace, db_key))");
			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void clear(final String namespace) {
		try (Connection connection = pool.getConnection();
				PreparedStatement st = connection.prepareStatement("DELETE FROM configuration WHERE db_namespace = ?")) {

			st.setString(1, namespace);
			st.execute();

			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void clearAll () {
		try (Connection connection = pool.getConnection();
				PreparedStatement st = connection.prepareStatement("DELETE FROM configuration")) {

			st.execute();

			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String get(final String namespace, final String key) {
		try (Connection connection = pool.getConnection()) {

			String statement = "SELECT * FROM configuration WHERE db_namespace = ? AND db_key = ?";

			try (PreparedStatement ps = connection.prepareStatement(statement)) {
				ps.setString(1, namespace);
				ps.setString(2, key);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						final String content = clobToString(rs.getClob("db_content"));
						return content;
					}
				}

			}
			return null;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public int count(final String namespace) {
		try (Connection connection = pool.getConnection()) {

			String statement = "SELECT count(db_key) as count FROM configuration WHERE db_namespace = ?";

			try (PreparedStatement ps = connection.prepareStatement(statement)) {
				ps.setString(1, namespace);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						return rs.getInt("count");
					}
				}

			}
			return 0;
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean add(final String namespace, final String key, final String content) {

		try (Connection connection = pool.getConnection()) {

			String statement = "MERGE INTO configuration (db_namespace, db_key, db_content) VALUES(?, ?, ?)";

			try (PreparedStatement ps = connection.prepareStatement(statement)) {

				ps.setString(1, namespace);
				ps.setString(2, key);
				ps.setClob(3, new StringReader(content));
				ps.execute();

				connection.commit();

				return true;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Delete an entity and all attributes;
	 *
	 * @param namespace
	 */
	@Override
	public void delete(final String namespace) {
		try (Connection connection = pool.getConnection();
				PreparedStatement st = connection.prepareStatement("DELETE FROM configuration WHERE db_namespace = ?")) {

			st.setString(1, namespace);
			st.execute();

			connection.commit();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Close the entities instance and shutdown the database connection.
	 */
	public void close() {
		try {
			pool.dispose();
		} catch (Exception ex) {
			LOGGER.error("", ex);
			throw new RuntimeException(ex);
		}
	}

	public static H2DB create(final File path) {
		return new H2DB(path);
	}

	private String clobToString(java.sql.Clob data) {
		final StringBuilder sb = new StringBuilder();

		try {
			final Reader reader = data.getCharacterStream();
			final BufferedReader br = new BufferedReader(reader);

			int b;
			while (-1 != (b = br.read())) {
				sb.append((char) b);
			}

			br.close();
			return sb.toString();
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
