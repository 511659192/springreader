// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.io;

import java.io.InputStream;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/30 4:22 下午
 **/
public interface InputStreamSource {

    InputStream getInputStream();
}