package com.sdi.sutom.helper;

public record SutomRequest(
        String pattern, // AT....D. (ATTITUDE)
        String inclusions, // Comma separated letters (A,B,C,D)
        String exclusions // Comma separated letters (R,T,U,V)
) {
}
