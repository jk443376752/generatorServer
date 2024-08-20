package com.tool4j.generator.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Deng.Weiping
 * @since 2023/8/17 15:14
 */
@Data
@Accessors(chain = true)
public class TreeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String key;

    /**
     * 是否是叶子节点
     */
    @JsonProperty("isLeaf")
    private Boolean leaf;

    private List<TreeVO> children;

    public TreeVO addChildren(TreeVO treeVO) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(treeVO);
        return this;
    }
}
