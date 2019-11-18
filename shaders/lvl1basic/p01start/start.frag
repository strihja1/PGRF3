#version 150
out vec4 outColor;// output from the fragment shader
in vec3 vertColor;
in vec3 normal;
in vec3 light;
in vec3 viewDirection;
in vec4 depthTextureCoord;
in vec2 texCoord;
uniform float type;
uniform sampler2D textureMosaic;
uniform float mode;
uniform sampler2D depthTexture;
in vec3 testNormala;
void main() {
    vec4 ambient = vec4(vec3(0.15), 1);
    float NdotL = max(0, dot(normalize(normal), normalize(light)));
    vec4 diffuse = vec4(NdotL*vec3(0.35), 1);
    vec3 halfVector = normalize(normalize(light)+normalize(viewDirection));
    float NdotH = dot(normalize(normal), halfVector);
    vec4 specular = vec4(pow(NdotH, 16) * vec3(1), 1);
    vec4 finalColor = diffuse + ambient + specular;
    vec4 textureColor = texture(textureMosaic, texCoord);
    float zL = texture(depthTexture, depthTextureCoord.xy).r;// R G i B složky jsou stejné, protože gl_fragcoord.zzz
    float zA = depthTextureCoord.z;

    bool shadow = zL < zA - 0.005;

    if (shadow){
        outColor = ambient*textureColor;
        //outColor = diffuse + ambient + specular ;

    } else {
        outColor = finalColor*textureColor;
    }
    if (type == 2.0){
        outColor = vec4(1, 1, 0, 1);
    }
    if (mode == 1.0f){
        outColor = normalize(vec4(normalize(normal), 1));
    }
    if (mode == 2.0f){
        outColor = normalize(vec4(normalize(vertColor), 1));
    }

if (mode == 3.0f){
    vec3 nNormal = normalize(testNormala);
    float f = dot(normalize(vec3(0.0, 1.0, 1.0)), nNormal);
    f = max(f, 0.0);
    outColor.rgb = vec3(f);
    outColor.a=1.0;
}
}
