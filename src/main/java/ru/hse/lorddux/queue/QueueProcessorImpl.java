package ru.hse.lorddux.queue;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.hse.lorddux.http.DeleteMessageQueueRequest;
import ru.hse.lorddux.http.GetMessageQueueRequest;
import ru.hse.lorddux.structures.TaskItem;
import ru.hse.lorddux.structures.request.DeleteMessageQueueRequestData;
import ru.hse.lorddux.structures.request.GetMessageQueueRequestData;
import ru.hse.lorddux.utils.XMLTaskParser;

import java.beans.XMLDecoder;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class QueueProcessorImpl implements QueueProcessor {
    private static final Logger log_ = LogManager.getLogger(QueueProcessor.class);
    private static String GET_MESSAGE_PATH = "/messages";
    private static String DELETE_MESSAGE_PATH = "/messages/messageid";
    private static Integer DEFAULT_BATCH_SIZE = 50;

    private String queueAuthorization;
    private String queueHost;
    private String getMessagePath;
    private String deleteMessagePath;


    public QueueProcessorImpl(String queueAccount, String queueName, String queueAuthorization){
        this.queueHost = buildQueueHost(queueAccount);
        this.getMessagePath = buildQueuePath(queueName, GET_MESSAGE_PATH);
        this.deleteMessagePath = buildQueuePath(queueName, DELETE_MESSAGE_PATH);
        this.queueAuthorization = queueAuthorization;
    }

    @Override
    public List<TaskItem> getNextBatch(int visibilityTimeout) {
        return getNextBatch(DEFAULT_BATCH_SIZE, visibilityTimeout);
    }

    @Override
    public List<TaskItem> getNextBatch(int batchSize, int visibilityTimeout) {
        GetMessageQueueRequestData requestData = new GetMessageQueueRequestData(batchSize, visibilityTimeout);
        GetMessageQueueRequest request = new GetMessageQueueRequest(requestData, queueAuthorization, queueHost, getMessagePath);
        try {
            String xmlTasks = request.execute();
            return XMLTaskParser.parse(xmlTasks);
        } catch (Exception e) {
            log_.warn("Can not get task", e);
        }
        return Collections.emptyList();
    }

    @Override
    public TaskItem getNextTask(int visibilityTimeout) {
        List<TaskItem> taskItems = getNextBatch(1, visibilityTimeout);
        return taskItems.size() == 1 ? taskItems.get(0) : null;
    }

    @Override
    public boolean deleteTask(String popReceipt) {
        DeleteMessageQueueRequestData requestData = new DeleteMessageQueueRequestData(popReceipt);
        DeleteMessageQueueRequest request = new DeleteMessageQueueRequest(requestData, queueAuthorization, queueHost, deleteMessagePath);
        try {
            request.execute();
            return true;
        } catch (Exception e) {
            log_.warn("Can not delete task from queue", e);
            return false;
        }
    }

    /**
     * Example:
     * "https://myaccount.queue.core.windows.net"
     * @return
     */
    private String buildQueueHost(String queueAccount) {
        StringBuilder host = new StringBuilder();
        host.append(queueAccount);
        host.append(".queue.core.windows.net");
        return host.toString();
    }

    /**
     * "/myqueue/messages"
     * @param queueName
     * @param methodPath
     * @return
     */
    private String buildQueuePath(String queueName, String methodPath) {
        StringBuilder pathBuilder = new StringBuilder("/");
        pathBuilder.append(queueName);
        pathBuilder.append(methodPath);
        return pathBuilder.toString();
    }
}