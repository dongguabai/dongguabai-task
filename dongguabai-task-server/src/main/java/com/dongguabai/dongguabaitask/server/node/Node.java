package com.dongguabai.dongguabaitask.server.node;

import lombok.Data;

/**
 * @author Dongguabai
 * @Description
 * @Date 创建于 2020-06-20 17:22
 */
@Data
public class Node {

    private boolean master = false;

    private boolean available = true;

    private String nodePath;

    private boolean genJob = false;

    private boolean execute = false;

    /**
     * 节点序号,从 0 开始
     */
    private int seq;

    @Override
    public String toString() {
        return "Node[" +
                "nodePath='" + nodePath + '\'' +
                ", seq=" + seq +
                ']';
    }
}
