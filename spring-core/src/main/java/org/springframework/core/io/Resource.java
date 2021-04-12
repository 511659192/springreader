// Copyright (C) 2021 Meituan
// All rights reserved
package org.springframework.core.io;

import java.io.File;
import java.net.URL;

/**
 * @author yangmeng
 * @version 1.0
 * @created 2021/3/30 4:21 下午
 **/
public interface Resource extends InputStreamSource{

    URL getURL();

    File getFile();
}