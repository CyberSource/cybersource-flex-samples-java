/**
 * Copyright (c) 2016 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.flex.vdp;

/**
 * Runtime exception raised, when unable to properly sign VDP request.
 */
public final class VDPSignatureException extends RuntimeException {

    VDPSignatureException(final Throwable t) {
        super(t);
    }

}
