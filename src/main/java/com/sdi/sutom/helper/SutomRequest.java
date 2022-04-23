package com.sdi.sutom.helper;

public record SutomRequest(
        String pattern, // AT....D. (ATTITUDE)
        String inclusions, // Eg. ABCD
        String exclusions // Eg. OPQR
) {
}
