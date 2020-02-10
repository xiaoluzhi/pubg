package com.tencent.tga.liveplugin.live.common.util;

import java.util.LinkedList;

/**
 * Created by fluxliu on 2016/8/26.
 */
public class LimitQueue<E> {

    private int limit; // 队列长度

    public LinkedList<E> queue = new LinkedList<E>();

    public LimitQueue(int limit){
        this.limit = limit;
    }

    /**
     * 入列：当队列大小已满时，把队头的元素poll掉
     */
    public synchronized void offer(E e){
        try {
            if(queue.size() >= limit){
                queue.poll();
            }
            queue.offer(e);
        }catch (Exception ecx){
            ecx.printStackTrace();
        }

    }

    public synchronized E poll(){
        return queue.poll();
    }


    public synchronized void clear(){
        synchronized (queue)
        {
            queue.clear();
        }

    }

    public synchronized E get(int position) {
        return queue.get(position);
    }

    public synchronized  E getLast() {
        return queue.getLast();
    }

    public synchronized E getFirst() {
        return queue.getFirst();
    }

    public synchronized int getLimit() {
        return limit;
    }

    public synchronized int size() {
        return queue.size();
    }
}
