// Copyright (C) 2021 Meituan
// All rights reserved
package springframework.context.support;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * @author Administrator
 * @version 1.0
 * @created 2021/5/18 23:16
 **/
public class BlockJUnit4ClassRunnerEx extends BlockJUnit4ClassRunner {
    public BlockJUnit4ClassRunnerEx(Class<?> testClass) throws InitializationError {
        super(testClass);
    }
}