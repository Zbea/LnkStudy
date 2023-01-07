package com.bll.lnkstudy.mvp.model;

import java.util.List;

public class CityBean {

    public List<ProvincesDTO> provinces;

    public static class ProvincesDTO {

        public List<CitysDTO> citys;
        public String provinceName;

        public static class CitysDTO {
            public String citysName;
        }
    }
}
