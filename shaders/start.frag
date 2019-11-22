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
uniform float spotCutOff;
in vec3 spotDirection;
in vec3 lightSpot;
uniform sampler2D depthTexture;
in float lightDistance;
uniform float reflector;
in float intensity;
const float constantAttenuation = 0.3;
const float linearAttenuation = 0.004;
const float quadraticAttenuation = 0.004;

varying float dist;


void main() {
    vec4 ambient = vec4(vec3(0.3), 1);
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
        if(reflector==1.0f){
            float att=1.0/(constantAttenuation + linearAttenuation * lightDistance + quadraticAttenuation * lightDistance * lightDistance);
            float spotEffect = max(dot(normalize(spotDirection), normalize(-normalize(lightSpot))), 0);
            float blend = clamp((spotEffect-spotCutOff)/(1-spotCutOff),0.0,1.0); //orezani na rozsah <0;1>
            if (spotEffect > spotCutOff) {
                outColor= mix(ambient,ambient+att*(diffuse+specular),blend)*textureColor;
            } else
            outColor= ambient*textureColor;
        }else{

            float att=1.0/(constantAttenuation + linearAttenuation*100 * dist + quadraticAttenuation * dist * dist);
            outColor= finalColor*textureColor+att*(diffuse+specular);
        }
    }
    if (type == 2.0f){
        outColor = vec4(1, 1, 0, 1);
    }
    if (mode == 1.0f){
        outColor = normalize(vec4(normalize(normal), 1));
    }
    if (mode == 2.0f){
        outColor = normalize(vec4(normalize(vertColor), 1));
    }
    if (mode == 3.0f){
    outColor = normalize(vec4(texCoord,0,1));
    }
    if (mode == 4.0f){
        outColor = depthTextureCoord;
    }

    if(mode==5.0f){
        vec4 color;
        if (intensity>0.95) color=vec4(1.0,0.5,0.5,1.0);
        else if (intensity>0.8) color=vec4(0.6,0.3,0.3,1.0);
        else if (intensity>0.5) color=vec4(0.0,0.0,0.3,1.0);
        else if (intensity>0.25) color=vec4(0.4,0.2,0.2,1.0);
        else color=vec4(0.2,0.1,0.1,1.0);
        outColor = vec4(color);
    }

    if(mode==6.0f){
        float intensity = dot(normalize(lightSpot), normalize(normal));;
        vec4 color;
        if (intensity>0.95) color=vec4(1.0,0.5,0.5,1.0);
        else if (intensity>0.8) color=vec4(0.6,0.3,0.3,1.0);
        else if (intensity>0.5) color=vec4(0.0,0.0,0.3,1.0);
        else if (intensity>0.25) color=vec4(0.4,0.2,0.2,1.0);
        else color=vec4(0.2,0.1,0.1,1.0);
        outColor = vec4(color);
    }
}

