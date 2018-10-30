package com.taj.entity.school_tenders_by_school;

import javax.persistence.*;

/**
 * Created by User on 10/16/2018.
 */
@Entity
@Table(name = "school_requst_images")
public class SchoolRequestImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int image_id;
    private byte[] image_one;
    private byte[] image_two;
    private byte[] images_three;
    private byte[] image_four;

    public SchoolRequestImageEntity(int image_id, byte[] image_one, byte[] image_two, byte[] images_three, byte[] image_four) {
        this.image_id = image_id;
        this.image_one = image_one;
        this.image_two = image_two;
        this.images_three = images_three;
        this.image_four = image_four;
    }

    public SchoolRequestImageEntity(byte[] image_one, byte[] image_two, byte[] images_three, byte[] image_four) {
        this.image_one = image_one;
        this.image_two = image_two;
        this.images_three = images_three;
        this.image_four = image_four;
    }

    public SchoolRequestImageEntity() {
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public byte[] getImage_one() {
        return image_one;
    }

    public void setImage_one(byte[] image_one) {
        this.image_one = image_one;
    }

    public byte[] getImage_two() {
        return image_two;
    }

    public void setImage_two(byte[] image_two) {
        this.image_two = image_two;
    }

    public byte[] getImages_three() {
        return images_three;
    }

    public void setImages_three(byte[] images_three) {
        this.images_three = images_three;
    }

    public byte[] getImage_four() {
        return image_four;
    }

    public void setImage_four(byte[] image_four) {
        this.image_four = image_four;
    }
}
