package com.thoughtworks.firenze.texas.holdem.domain;

import com.thoughtworks.firenze.texas.holdem.domain.enums.Action;
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
public class Operation {
    private Action action;
}
