#include "native-lib.h"
#include <string>
#include <thread>
#include <vector>
#include <node.h>
#include <unistd.h>

/*
 * Most of this is modified from https://github.com/sixo/node-example
 */

extern "C"
JNIEXPORT void JNICALL
Java_com_mafintosh_nodeonandroid_NodeService_startNode(JNIEnv *env, jobject instance, jobjectArray args) {

    nodeonandroid::redirectStreamsToPipe();
    nodeonandroid::startLoggingFromPipe();

    auto continuousArray = nodeonandroid::makeContinuousArray(env, args);
    auto argv = nodeonandroid::getArgv(continuousArray);

    node::Start(argv.size() - 1, argv.data());
}

namespace nodeonandroid {
    namespace {
        std::thread logger;
        static int pfd[2];
    }

    std::vector<char> makeContinuousArray(JNIEnv *env, jobjectArray fromArgs) {
        int count = env->GetArrayLength(fromArgs);
        std::vector<char> buffer;
        for (int i = 0; i < count; i++) {
            jstring str = (jstring)env->GetObjectArrayElement(fromArgs, i);
            const char* sptr = env->GetStringUTFChars(str, 0);

            do {
                buffer.push_back(*sptr);
            }
            while(*sptr++ != '\0');
        }

        return buffer;
    }

    std::vector<char*> getArgv(std::vector<char>& fromContinuousArray) {
        std::vector<char*> argv;

        argv.push_back(fromContinuousArray.data());
        for (int i = 0; i < fromContinuousArray.size() - 1; i++) {
            if (fromContinuousArray[i] == '\0') argv.push_back(&fromContinuousArray[i+1]);
        }

        argv.push_back(nullptr);

        return argv;
    }

    void redirectStreamsToPipe() {
        setvbuf(stdout, 0, _IOLBF, 0);
        setvbuf(stderr, 0, _IONBF, 0);

        pipe(pfd);
        dup2(pfd[1], 1);
        dup2(pfd[1], 2);
    }

    void startLoggingFromPipe() {
        logger = std::thread([](int *pipefd) {
            char buf[128];
            std::size_t nBytes = 0;
            while ((nBytes = read(pfd[0], buf, sizeof buf - 1)) > 0) {
                if (buf[nBytes - 1] == '\n') --nBytes;
                buf[nBytes] = 0;
                LOGD("%s", buf);
            }
        }, pfd);

        logger.detach();
    }
}