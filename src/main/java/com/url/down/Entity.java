package com.url.down;

import lombok.Data;

/**
 * @ClassName entity
 * @Description TODO
 * @Author Le
 * @Date 2021/5/23 17:31
 * @Version 1.0
 */
@Data
public class Entity {

    private String id;
    private String free;
    private String titleImageUrl;
    private boolean trial;
    private String title;
    private String mediaFilesize;
    private String publishTime;
    private String readCount;
    private String duration;
    private String audioUrl;
    private String topFlag;
    private String fragmentId;
}
