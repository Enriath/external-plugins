package io.hydrox.contextualcursor;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Skin
{
    OSRS("OldSchool"),
    RS2("Runescape 2");

    private final String name;

}