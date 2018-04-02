package ru.hse.lorddux.queue;

import lombok.AllArgsConstructor;
import ru.hse.lorddux.http.GetMessageQueueRequest;
import ru.hse.lorddux.structures.TaskItem;
import ru.hse.lorddux.structures.request.GetMessageQueueRequestData;

import java.util.List;

public class QueueProcessorImpl implements QueueProcessor {
    private static String GET_MESSAGE_PATH = "/messages";

    private String queueAuthorization;
    private String queueHost;
    private String getMethodPath;


    public QueueProcessorImpl(String queueAccount, String queueName){
        this.queueHost = buildQueueHost(queueAccount);
        this.getMethodPath = buildQueuePath(queueName, GET_MESSAGE_PATH);
    }

    @Override
    public List<TaskItem> getNextBatch(int visibilityTimeout) {
        return null;
    }

    @Override
    public List<TaskItem> getNextBatch(int batchSize, int visibilityTimeout) {
        GetMessageQueueRequestData requestData = new GetMessageQueueRequestData(batchSize, visibilityTimeout);
        GetMessageQueueRequest request = new GetMessageQueueRequest(requestData, queueAuthorization, queueHost, GET_MESSAGE_PATH);
        return null;
    }

    @Override
    public TaskItem getNextTask(int visibilityTimeout) {
        return null;
    }

    @Override
    public void deleteTask(String popReceipt) {
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