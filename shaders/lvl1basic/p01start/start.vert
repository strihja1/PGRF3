#version 150
in vec2 inPosition; // input from the vertex buffer


uniform mat4 view;
uniform mat4 projection;
uniform float time;   // in je pro každý vrchol jiný, uniform je stejný
uniform mat4 lightViewProjection;
out vec3 vertColor;
out vec3 normal;
out vec3 light;

out vec3 viewDirection;
out vec4 depthTextureCoord;
out vec2 texCoord;

uniform float type;
uniform float teleso;
uniform float posX, posY, posZ;
out vec2 pos;

float getZ(vec2 vec){
	return sin(time + vec.y*3.14);
}

vec3 getSphere(vec2 vec){
	float az = vec.x * 3.14f;
	float ze = vec.y * 3.14f/2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y = r * sin(az) * cos(ze);
	float z = r * sin(ze);
	return vec3(x, y, z);
}

vec3 getSun(vec2 vec){
	float az = vec.x * 3.14f;
	float ze = vec.y * 3.14f/2;
	float r = 0.3;

	float x = posX+ r * cos(az) * cos(ze);
	float y =   posY+ r * sin(az) * cos(ze);
	float z =     posZ + r * sin(ze);
	return vec3(x, y, z);
}

vec3 getWTF(vec2 vec){
/*	float az = vec.x * 3.14f;
	float ze = vec.y * 3.14f/2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y = r * sin(az) * cos(ze);
	float z = sin(time+vec.y);
	return vec3(x, y, z);*/

	float x = vec.x;
	float y = vec.y;
	float z = sin(time+x);
	return vec3(x,y,z);
}

vec3 getWeirdSphere(vec2 vec){
	float az = vec.x * 3.14f;
	float ze = vec.y * 3.14f/2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y =  2*  r * sin(az) * cos(ze);
	float z =       r * sin(ze);
	return vec3(x, y, z);
}
vec3 getPlane(vec2 vec){
	return vec3(vec*4,-1);
}
vec3 getMovingPlane(vec2 vec){
	return vec3(vec*4,getZ(vec));
}


vec3 getSphereNormal(vec2 vec){
	vec3 u = getSphere(vec + vec2(0.001,0))-getSphere(vec - vec2(0.001,0));
	vec3 v = getSphere(vec + vec2(0,0.001))-getSphere(vec - vec2(0,0.001));
	return cross(u,v);
}

vec3 getWeirdSphereNormal(vec2 vec){
	vec3 u = getWeirdSphere(vec + vec2(0.001,0))-getWeirdSphere(vec - vec2(0.001,0));
	vec3 v = getWeirdSphere(vec + vec2(0,0.001))-getWeirdSphere(vec - vec2(0,0.001));
	return cross(u,v);
}

vec3 getWTFNormal(vec2 vec){
	vec3 u = getWTF(vec + vec2(0.001,0))-getWTF(vec - vec2(0.001,0));
	vec3 v = getWTF(vec + vec2(0,0.001))-getWTF(vec - vec2(0,0.001));
	return cross(u,v);
}

vec3 getPlaneNormal(vec2 vec){
	vec3 u = getPlane(vec + vec2(0.001,0))-getPlane(vec - vec2(0.001,0));
	vec3 v = getPlane(vec + vec2(0,0.001))-getPlane(vec - vec2(0,0.001));
	return cross(u,v);
}

void main() {
	vec2 pos = inPosition *2-1; // je od -1 do +1
	vec4 pos4;

	//vec4 pos4 = vec4(pos, getZ(pos), 1.0);
	// type 1 - objekty
	// type 2 - podstava
	// type 3 - slunce
	if(type == 1.0){

		if(teleso==1.0){
			pos4 =  vec4(getWTF(pos), 1.0);
			normal = mat3(view)*getWTFNormal(pos);
		}
		else if (teleso == 0.0){
		pos4 = vec4(getWeirdSphere(pos), 1.0);
		normal = mat3(view)*getWeirdSphereNormal(pos);
		}
	}else if(type==0){
		pos4 = vec4(getPlane(pos),1);

		normal = mat3(view)*getPlaneNormal(pos);
	}else if(type==2.0){
		pos4 = vec4(getSun(pos),1);

		normal = mat3(view)*getSphereNormal(pos);
	}

	gl_Position = projection * view * pos4;

	vec3 lightPos = vec3(1,1,0);
	light = lightPos - (view* pos4).xyz;

	//viewDirection = -pos4.xyz;
	viewDirection= - (view* pos4).xyz;
		texCoord = inPosition;

	depthTextureCoord = lightViewProjection * pos4;
	depthTextureCoord.xyz = depthTextureCoord.xyz/ depthTextureCoord.w;
	depthTextureCoord.xyz = (depthTextureCoord.xyz+1)/2;

	//vertColor = pos4.xyz;    // pro projekt je pak dobré to povolit
} 
