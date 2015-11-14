package com.aexp.jmai.qlexam.domain;

import java.util.List;

public class GiantBomb {
    public String        error;
    public String        number_of_total_results;
    public List<Results> results;

    public class Results {
        public String name;
        public Image  image;

        public class Image {
            public String icon_url;
            public String thumb_url;
        }
    }
}
