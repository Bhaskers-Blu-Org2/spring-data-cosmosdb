/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository.query;

import com.microsoft.azure.spring.data.cosmosdb.core.CosmosOperations;
import com.microsoft.azure.spring.data.cosmosdb.core.query.CosmosPageRequest;
import com.microsoft.azure.spring.data.cosmosdb.core.query.DocumentQuery;
import org.springframework.data.domain.Pageable;

public interface CosmosQueryExecution {
    Object execute(DocumentQuery query, Class<?> type, String container);

    final class ContainerExecution implements CosmosQueryExecution {

        private final CosmosOperations operations;

        public ContainerExecution(CosmosOperations operations) {
            this.operations = operations;
        }

        @Override
        public Object execute(DocumentQuery query, Class<?> type, String container) {
            return operations.getContainerName(type);
        }
    }

    final class MultiEntityExecution implements CosmosQueryExecution {

        private final CosmosOperations operations;

        public MultiEntityExecution(CosmosOperations operations) {
            this.operations = operations;
        }

        @Override
        public Object execute(DocumentQuery query, Class<?> type, String container) {
            return operations.find(query, type, container);
        }
    }

    final class ExistsExecution implements CosmosQueryExecution {

        private final CosmosOperations operations;

        public ExistsExecution(CosmosOperations operations) {
            this.operations = operations;
        }

        @Override
        public Object execute(DocumentQuery query, Class<?> type, String container) {
            return operations.exists(query, type, container);
        }
    }

    final class DeleteExecution implements CosmosQueryExecution {

        private final CosmosOperations operations;

        public DeleteExecution(CosmosOperations operations) {
            this.operations = operations;
        }

        @Override
        public Object execute(DocumentQuery query, Class<?> type, String container) {
            return operations.delete(query, type, container);
        }
    }

    final class PagedExecution implements CosmosQueryExecution {
        private final CosmosOperations operations;
        private final Pageable pageable;

        public PagedExecution(CosmosOperations operations, Pageable pageable) {
            this.operations = operations;
            this.pageable = pageable;
        }

        @Override
        public Object execute(DocumentQuery query, Class<?> type, String container) {
            if (pageable.getPageNumber() != 0 && !(pageable instanceof CosmosPageRequest)) {
                throw new IllegalStateException("Not the first page but Pageable is not a valid " +
                        "CosmosPageRequest, requestContinuation is required for non first page request");
            }

            query.with(pageable);

            return operations.paginationQuery(query, type, container);
        }
    }
}
