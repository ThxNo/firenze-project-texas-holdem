package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String name;
    private Integer bettingChips;
    private Integer totalChip;

    public Integer pet(Integer followChip) {
        if (bettingChips >= followChip) {
            return 0;
        }
        int sub = followChip - bettingChips;
        bettingChips = followChip;
        return sub;
    }

    public Integer raise(Integer followChip) {
        if (bettingChips >= followChip * Constants.RAISE_MULTIPLE) {
            return 0;
        }
        int sub = followChip * Constants.RAISE_MULTIPLE - bettingChips;
        bettingChips = followChip * Constants.RAISE_MULTIPLE;
        return sub;
    }
}
