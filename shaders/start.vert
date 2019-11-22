#version 150
in vec2 inPosition;
uniform mat4 view;
uniform mat4 viewLight;
uniform mat4 projection;
uniform float time;
uniform mat4 lightViewProjection;
out vec3 vertColor;
out vec3 normal;
out vec3 light;
out vec3 viewDirection;
out vec4 depthTextureCoord;
out vec2 texCoord;
out vec3 spotDirection;
out vec3 lightSpot;
out float lightDistance;
uniform float type;
uniform float teleso;
uniform float posX, posY, posZ;
out vec2 pos;
uniform float pi = 3.14159265359;
uniform float mode;
varying float dist;
uniform float reflector;
out float intensity;

float getZ(vec2 vec){
    return sin(time + vec.y*pi);
}

vec3 getSphere(vec2 vec){
    float az = vec.x * pi;
    float ze = vec.y * pi/2;
    float r = 1;

    float x = r * cos(az) * cos(ze);
    float y = r * sin(az) * cos(ze);
    float z = r * sin(ze);
    return vec3(x, y, z);
}

vec3 getSun(vec2 vec){
    float az = vec.x * pi;
    float ze = vec.y * pi/2;
    float r = 0.3;

    float x = posX+ r * cos(az) * cos(ze);
    float y = posY+ r * sin(az) * cos(ze);
    float z = posZ + r * sin(ze);
    return vec3(x, y, z);
}

vec3 getWTF(vec2 vec){
    float s = vec.x * sin(pi/2);
    float t = vec.y * pi;
    float rho = 1+0.2*sin(2*s)*sin(-3*t);
    float phi = t;
    float theta = s;

    float x = rho * cos(phi) * cos(theta);
    float y = rho * sin(phi) * cos(theta);
    float z = rho * sin(theta);
    return vec3(x, y, z);

}

vec3 getWeirdSphere(vec2 vec){
    float az = vec.x * pi;
    float ze = vec.y * pi/2;
    float r = 1;

    float x = r * cos(az) * cos(ze);
    float y = 2*  r * sin(az) * cos(ze);
    float z = r * sin(ze);
    return vec3(x, y, z);
}

vec3 getParsur(vec2 vec){
    float s = pi*vec.x;
    float t = vec.y;
    float x = t* cos(s);
    float y =  t * sin(s);
    float z =t;
    return vec3(x, y, z);
}
vec3 getParsur2(vec2 vec){
    float s = pi*vec.x;
    float t = vec.y;
    float x = pow(4/3,t)*2*sin(t)*cos(s);
    float y = pow(4/3,t)*sin(t)*sin(s);
    float z = pow(4/3,s)*2*sin(t)*cos(t);
    return vec3(x, y, z);
}

vec3 getCylinder(vec2 vec){
    float s = pi*vec.x;
    float t = vec.y;
    float r = 1;
    float theta =  s;
    float z = t;
    return vec3(r*cos(theta), r*sin(theta), z);
}
vec3 getTrophy(vec2 vec) {
    float s = pi*vec.x;
    float t = pi*vec.y;
    float r = (1-max(sin(t), 0))*2;
    float theta =  -s;
    float z = 3-t;
    return vec3(r*cos(theta)/3, r*sin(theta)/3, z/3-1);
}

vec3 getBumpySphere(vec2 vec){
    float s = vec.x * pi/2;
    float t = vec.y * pi;
    float rho = 1+0.2*sin(6*s)*sin(5*t);
    float phi = t;
    float theta = s;

    float x = rho * cos(phi) * cos(theta);
    float y = rho * sin(phi) * cos(theta);
    float z = rho * sin(theta);
    return vec3(x, y, z);
}


vec3 getPlane(vec2 vec){
    return vec3(vec*4, -1);
}
vec3 getMovingPlane(vec2 vec){
    return vec3(vec*cos(vec.x), getZ(vec));
}
vec3 getMovingPlane2(vec2 vec){
    return vec3(vec*cos(vec.x)+1.5, getZ(vec));
}

vec3 getSphereNormal(vec2 vec){
    vec3 u = getSphere(vec + vec2(0.001, 0))-getSphere(vec - vec2(0.001, 0));
    vec3 v = getSphere(vec + vec2(0, 0.001))-getSphere(vec - vec2(0, 0.001));
    return cross(u, v);
}

vec3 getCylinderNormal(vec2 vec){
    vec3 u = getCylinder(vec + vec2(0.001, 0))-getCylinder(vec - vec2(0.001, 0));
    vec3 v = getCylinder(vec + vec2(0, 0.001))-getCylinder(vec - vec2(0, 0.001));
    return cross(u, v);
}

vec3 getTrophyNormal(vec2 vec){
    vec3 u = getTrophy(vec + vec2(0.001, 0))-getTrophy(vec - vec2(0.001, 0));
    vec3 v = getTrophy(vec + vec2(0, 0.001))-getTrophy(vec - vec2(0, 0.001));
    return cross(u, v);
}
vec3 getBumpySphereNormal(vec2 vec){
    vec3 u = getBumpySphere(vec + vec2(0.001, 0))-getBumpySphere(vec - vec2(0.001, 0));
    vec3 v = getBumpySphere(vec + vec2(0, 0.001))-getBumpySphere(vec - vec2(0, 0.001));
    return cross(u, v);
}
vec3 getWeirdSphereNormal(vec2 vec){
    vec3 u = getWeirdSphere(vec + vec2(0.001, 0))-getWeirdSphere(vec - vec2(0.001, 0));
    vec3 v = getWeirdSphere(vec + vec2(0, 0.001))-getWeirdSphere(vec - vec2(0, 0.001));
    return cross(u, v);
}

vec3 getWTFNormal(vec2 vec){
    vec3 u = getWTF(vec + vec2(0.001, 0))-getWTF(vec - vec2(0.001, 0));
    vec3 v = getWTF(vec + vec2(0, 0.001))-getWTF(vec - vec2(0, 0.001));
    return cross(u, v);
}
vec3 getParsurNormal(vec2 vec){
    vec3 u = getParsur(vec + vec2(0.001, 0))-getParsur(vec - vec2(0.001, 0));
    vec3 v = getParsur(vec + vec2(0, 0.001))-getParsur(vec - vec2(0, 0.001));
    return cross(u, v);
}
vec3 getParsur2Normal(vec2 vec){
    vec3 u = getParsur2(vec + vec2(0.001, 0))-getParsur2(vec - vec2(0.001, 0));
    vec3 v = getParsur2(vec + vec2(0, 0.001))-getParsur2(vec - vec2(0, 0.001));
    return cross(u, v);
}

vec3 getPlaneNormal(vec2 vec){
    vec3 u = getPlane(vec + vec2(0.001, 0))-getPlane(vec - vec2(0.001, 0));
    vec3 v = getPlane(vec + vec2(0, 0.001))-getPlane(vec - vec2(0, 0.001));
    return cross(u, v);
}
vec3 getMovingPlaneNormal(vec2 vec){
    vec3 u = getMovingPlane(vec + vec2(0.001, 0))-getMovingPlane(vec - vec2(0.001, 0));
    vec3 v = getMovingPlane(vec + vec2(0, 0.001))-getMovingPlane(vec - vec2(0, 0.001));
    return cross(u, v);
}
vec3 getMovingPlaneNormal2(vec2 vec){
    vec3 u = getMovingPlane(vec + vec2(0.001, 0))-getMovingPlane(vec - vec2(0.001, 0));
    vec3 v = getMovingPlane(vec + vec2(0, 0.001))-getMovingPlane(vec - vec2(0, 0.001));
    return cross(u, v);
}

void main() {
    vec2 pos = inPosition *2-1;// je od -1 do +1
    vec4 pos4;

    // type 0 - podstava
    // type 1 - objekty
    // type 2 - slunce
    // type 3 - druhy objekt
    if (type == 1.0){
        if (teleso == 0.0){
            pos4 = vec4(getWeirdSphere(pos), 1.0);
            normal = mat3(view)*getWeirdSphereNormal(pos);
        }
        else if (teleso==1.0){
            pos4 =  vec4(getParsur(pos), 1.0);
            normal = mat3(view)*getParsurNormal(pos);
        }
        else if (teleso == 2.0){
            pos4 = vec4(getMovingPlane(pos), 1.0);
            normal = mat3(view)*getMovingPlaneNormal(pos);
        }
        else if (teleso == 3.0){
            pos4 = vec4(getCylinder(pos), 1.0);
            normal = mat3(view)*getCylinderNormal(pos);
        }
        else if (teleso == 4.0){
            pos4 = vec4(getTrophy(pos), 1.0);
            normal = mat3(view)*getTrophyNormal(pos);
        }
        else if (teleso == 5.0){
            pos4 = vec4(getBumpySphere(pos), 1.0);
            normal = mat3(view)*getBumpySphereNormal(pos);
        }
        else if (teleso == 6.0){
            pos4 = vec4(getWTF(pos), 1.0);
            normal = mat3(view)*getWTFNormal(pos);
        }
        else if (teleso == 7.0){
            pos4 = vec4(getParsur2(pos), 1.0);
            normal = mat3(view)*getParsur2Normal(pos);
        }
    } else if (type==0){
        pos4 = vec4(getPlane(pos), 1);

        normal = mat3(view)*getPlaneNormal(pos);
    } else if (type==2.0){
        pos4 = vec4(getSun(pos), 1);

        normal = mat3(view)*getSphereNormal(pos);
    }
    if(type==3.0){
        if (teleso == 0.0){
            pos4 = vec4(getMovingPlane2(pos), 1.0);
            normal = mat3(view)*getMovingPlaneNormal2(pos);
        }

    }

    gl_Position = projection * view * pos4;

    vec3 lightPos = vec3(1, 1, 1);
    light = lightPos - (view* pos4).xyz;
    lightSpot= lightPos-(viewLight*pos4).xyz;
    viewDirection= - (view* pos4).xyz;
    spotDirection = -(lightViewProjection*pos4).xyz;
    texCoord = inPosition;
    depthTextureCoord = lightViewProjection * pos4;
    depthTextureCoord.xyz = depthTextureCoord.xyz/ depthTextureCoord.w;
    depthTextureCoord.xyz = (depthTextureCoord.xyz+1)/2;
    vertColor = pos4.xyz;
    lightDistance = length(-(viewLight*pos4).xyz);
    dist = length(viewDirection);
    if (mode==5.0f){
        intensity = dot(normalize(lightSpot), normalize(normal));
        vertColor = vec3(normal.xyz);
    }
}
