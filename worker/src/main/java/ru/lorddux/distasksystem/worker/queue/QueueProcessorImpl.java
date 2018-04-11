package ru.lorddux.distasksystem.worker.queue;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.queue.*;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

public final class QueueProcessorImpl implements QueueProcessor {
    private final CloudQueue queue;

    public QueueProcessorImpl(String storageConnectionString, String queueName)
            throws URISyntaxException, InvalidKeyException, StorageException {

        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        CloudQueueClient queueClient = storageAccount.createCloudQueueClient();
        queue = queueClient.getQueueReference(queueName);
    }

    @Override
    public CloudQueueMessage getNextTask() throws StorageException {
        return queue.retrieveMessage();
    }

    @Override
    public Iterable<CloudQueueMessage> getNextBatch(int batchSize) throws StorageException {
        batchSize = batchSize > 32 ? 32 : batchSize;
        return queue.retrieveMessages(batchSize);
    }

    @Override
    public CloudQueueMessage getNextTask(int visibilityTimeout) throws StorageException {
        return queue.retrieveMessage(visibilityTimeout, null, null);
    }

    @Override
    public Iterable<CloudQueueMessage> getNextBatch(int batchSize, int visibilityTimeout) throws StorageException {
        batchSize = batchSize > 32 ? 32 : batchSize;
        return queue.retrieveMessages(batchSize, visibilityTimeout, null, null);
    }

    @Override
    public void deleteTask(CloudQueueMessage message) throws StorageException {
        queue.deleteMessage(message);
    }

    public void putMessage(String message) throws Exception {
        queue.addMessage(new CloudQueueMessage(message));
    }

    public static void main(String[] args) throws Exception {

        QueueProcessorImpl queueProcessor = new QueueProcessorImpl("DefaultEndpointsProtocol=https;AccountName=testqueueaccount;AccountKey=KQIwlOlcIYV8hFjyN/Qpyo7lCvEkMj2yMVBEEkU7fnGrNaflj0pk7twV50dYwGDjBAm8VjmvfOA4rjJ/yvUmHQ==;EndpointSuffix=core.windows.net", "testqueue");
        for (int i = 0; i < 100; i++) {
            queueProcessor.putMessage("Kek" + String.valueOf(i));
        }

    }

}