package com.yline.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class LocationSwitchModel {
    public static class HotCityItemModel implements Serializable{
        private String name;

        private String region_id;

        public HotCityItemModel(String name, String region_id) {
            this.name = name;
            this.region_id = region_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }
    }

    /**
     * 下载时，整体格式
     */
    public class DownLoadModel implements Serializable {
        private List<HotCityItemModel> hot_city;

        private int version;

        private Map<String, List<DownLoadItemModel>> region_list;

        public List<HotCityItemModel> getHot_city() {
            return hot_city;
        }

        public void setHot_city(List<HotCityItemModel> hot_city) {
            this.hot_city = hot_city;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Map<String, List<DownLoadItemModel>> getRegion_list() {
            return region_list;
        }

        public void setRegion_list(Map<String, List<DownLoadItemModel>> region_list) {
            this.region_list = region_list;
        }
    }

    /**
     * 下载时，对应的小数据格式
     */
    public static class DownLoadItemModel implements Serializable {
        private String region_id;

        private String name; // 地区名称

        private String parent_name; // 上一级名称

        private String spell; // 拼字的， "," 作为分割

        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getParent_name() {
            return parent_name;
        }

        public void setParent_name(String parent_name) {
            this.parent_name = parent_name;
        }

        public String getSpell() {
            return spell;
        }

        public void setSpell(String spell) {
            this.spell = spell;
        }
    }

    /**
     * 搜索时，数据item格式
     */
    public static class SearchItemModel implements Serializable {
        private String region_id;

        private String name; // 地区名称

        private String parent_name; // 上一级名称

        public SearchItemModel(String region_id, String name, String parent_name) {
            this.region_id = region_id;
            this.name = name;
            this.parent_name = parent_name;
        }

        public String getRegion_id() {
            return region_id;
        }

        public void setRegion_id(String region_id) {
            this.region_id = region_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getParent_name() {
            return parent_name;
        }

        public void setParent_name(String parent_name) {
            this.parent_name = parent_name;
        }
    }
}
