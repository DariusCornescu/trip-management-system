package com.darius.project.repository.Database;

import com.darius.project.domain.Identifiable;
import com.darius.project.repository.MemoryRepository;
import org.slf4j.*;
import org.sqlite.*;
import java.sql.*;
import java.util.*;

public class RepoDB<ID, T extends Identifiable<ID>> extends MemoryRepository<ID, T> {

    protected Connection connection = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(RepoDB.class);

    public void openConnection() {
        LOGGER.info("Opening connection to database... - RepoDB");
        try {
            String URL = Config.getProperty("db.url");
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl(URL);
            if ( connection == null || connection.isClosed() ) {
                LOGGER.debug("DB URL: {}", URL);
                connection = dataSource.getConnection();
                LOGGER.info("Database connection opened successfully.");
            }
        }catch (SQLException e) {
            LOGGER.error("Error opening connection to database.", e);
        }
    }

    public void closeConnection() {
        LOGGER.info("Closing connection to database... - RepoDB");
        try {
            if ( connection != null ) {
                connection.close();
                LOGGER.info("Database connection closed successfully.");}
        }catch (SQLException e) {
            LOGGER.error("Error closing connection to database.", e);
        }
    }

    @Override
    public void save(ID id, T entity) { LOGGER.debug("Saving entity:  {} to database...", entity); }
    @Override
    public void delete(ID id) { LOGGER.debug("Deleting entity with id: {} from database...", id); }
    @Override
    public void update(ID id, T updatedEntity) { LOGGER.debug("Updating entity with id: {} into in {} database...", id, updatedEntity); }
    @Override
    public T findById(ID id) {
        LOGGER.debug("Searching for entity with id: {} in database...", id);
        return super.findById(id);
    }
    @Override
    public Iterator<T> findAll() {
        LOGGER.debug("Retrieving all entities from database...");
        return super.findAll();
    }
}
