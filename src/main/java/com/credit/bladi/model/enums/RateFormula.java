package com.credit.bladi.model.enums;

import com.credit.bladi.model.vo.Rate;

public enum RateFormula {
    MIN {
        @Override
        public Rate apply(Rate rateMin, Rate rateMax) {
            return rateMin;
        }
    },
    MAX {
        @Override
        public Rate apply(Rate rateMin, Rate rateMax) {
            return rateMax;
        }
    },
    AVERAGE {
        @Override
        public Rate apply(Rate rateMin, Rate rateMax) {
            return rateMin.average(rateMax);
        }
    };

    public abstract Rate apply(Rate rateMin, Rate rateMax);
}
