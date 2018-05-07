#include <jni.h>
#include <fstream>
#include <cstring>
#include <string>
#include <cstdlib>
#include <ctime>
#include "botan_all.h"

using namespace std;
using namespace Botan;


#ifdef __cplusplus
extern "C"  {
#endif

jint Java_pzy64_PzyCrypt_Pro_EncryptServiceX_Encrypt(JNIEnv *env, jobject obj, jstring i,
                                                     jstring o, jstring pass) {
    const char *password;
    const char *in;
    const char *out;
    try {
        Botan::LibraryInitializer init;
        ifstream inp;
        ofstream outp;
        SecureVector<byte> salt;
        SecureVector<byte> mMaster;
        KDF *kdf;
        SymmetricKey mKey;
        InitializationVector mIV;

        password = new char[env->GetStringUTFLength(pass)];
        in = new char[env->GetStringUTFLength(i)];
        out = new char[env->GetStringUTFLength(o)];

        password = env->GetStringUTFChars (pass,NULL);
        in = env->GetStringUTFChars (i,NULL);
        out = env->GetStringUTFChars (o,NULL);

        try {
            inp.open(in, ios::binary);
            outp.open(out, ios::binary);
        }catch(...){
            env->ReleaseStringUTFChars (pass,password);
            env->ReleaseStringUTFChars (o,out);
            env->ReleaseStringUTFChars (i,in);

            return 0;
        }

        salt.resize(48);
        for (int i = 0; i < salt.size(); i++)
            salt[i] = i;

        try{
            PKCS5_PBKDF2 pbkdf2(new HMAC(new SHA_256));

            kdf = get_kdf("KDF2(SHA-256)");

            mMaster = pbkdf2.derive_key(48, password, &salt[0], salt.size(),
                    2000).bits_of();
            mKey = kdf->derive_key(32, mMaster, "salt1");
            mIV = kdf->derive_key(16, mMaster, "salt2");
        }catch(...){
            env->ReleaseStringUTFChars (pass,password);
            env->ReleaseStringUTFChars (o,out);
            env->ReleaseStringUTFChars (i,in);
            return 0;
        }

        try  {
            Pipe pipe(get_cipher("AES-256/CBC/PKCS7", mKey, mIV, ENCRYPTION),
                      new DataSink_Stream(outp));
            pipe.start_msg();
            inp >> pipe;
            pipe.end_msg();
        }catch(...){
            env->ReleaseStringUTFChars (pass,password);
            env->ReleaseStringUTFChars (o,out);
            env->ReleaseStringUTFChars (i,in);
            return 0;
        }

        outp.flush();
        inp.close();
        outp.close();
        env->ReleaseStringUTFChars (pass,password);
        env->ReleaseStringUTFChars (o,out);
        env->ReleaseStringUTFChars (i,in);

        return 1;
    } catch (...) {
        env->ReleaseStringUTFChars (pass,password);
        env->ReleaseStringUTFChars (o,out);
        env->ReleaseStringUTFChars (i,in);
        return 0;
    }
}

jint Java_pzy64_PzyCrypt_Pro_DecryptServiceX_Decrypt(JNIEnv *env, jobject obj, jstring i,
                                                     jstring o, jstring pass) {
    const char *password;
    const char *in;
    const char *out;
    try {
        Botan::LibraryInitializer init;
        ifstream inp;
        ofstream outp;
        SecureVector<byte> salt;
        SecureVector<byte> mMaster;
        KDF *kdf;
        SymmetricKey mKey;
        InitializationVector mIV;

        password = new char[env->GetStringUTFLength(pass)];
        in = new char[env->GetStringUTFLength(i)];
        out = new char[env->GetStringUTFLength(o)];

        password = env->GetStringUTFChars (pass,NULL);
        in = env->GetStringUTFChars (i,NULL);
        out = env->GetStringUTFChars (o,NULL);

        try {
            inp.open(in, ios::binary);
            outp.open(out, ios::binary);
        }catch(...){
            env->ReleaseStringUTFChars (pass,password);
            env->ReleaseStringUTFChars (o,out);
            env->ReleaseStringUTFChars (i,in);

            return 0;
        }

        salt.resize(48);
        for (int i = 0; i < salt.size(); i++)
            salt[i] = i;

        try{
            PKCS5_PBKDF2 pbkdf2(new HMAC(new SHA_256));

            kdf = get_kdf("KDF2(SHA-256)");

            mMaster = pbkdf2.derive_key(48, password, &salt[0], salt.size(),
                    2000).bits_of();
            mKey = kdf->derive_key(32, mMaster, "salt1");
            mIV = kdf->derive_key(16, mMaster, "salt2");
        }catch(...){
            env->ReleaseStringUTFChars (pass,password);
            env->ReleaseStringUTFChars (o,out);
            env->ReleaseStringUTFChars (i,in);
            return 0;
        }

        try  {
            Pipe pipe(get_cipher("AES-256/CBC/PKCS7", mKey, mIV, DECRYPTION),
                      new DataSink_Stream(outp));
            pipe.start_msg();
            inp >> pipe;
            pipe.end_msg();
        }catch(...){
            env->ReleaseStringUTFChars (pass,password);
            env->ReleaseStringUTFChars (o,out);
            env->ReleaseStringUTFChars (i,in);
            return 0;
        }

        outp.flush();
        inp.close();
        outp.close();
        env->ReleaseStringUTFChars (pass,password);
        env->ReleaseStringUTFChars (o,out);
        env->ReleaseStringUTFChars (i,in);

        return 1;
    } catch (...) {
        env->ReleaseStringUTFChars (pass,password);
        env->ReleaseStringUTFChars (o,out);
        env->ReleaseStringUTFChars (i,in);
        return 0;
    }
}
#ifdef __cplusplus
}
#endif
