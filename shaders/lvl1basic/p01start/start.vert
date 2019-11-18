#version 150
in vec2 inPosition; // input from the vertex buffer
uniform mat4 view;
uniform mat4 projection;
uniform float time;   // in je pro každý vrchol jiný, uniform je stejný
uniform mat4 lightViewProjection;
out vec3 vertColor;
out vec3 normal;
out vec3 light;
out vec3 testNormala;
out vec3 viewDirection;
out vec4 depthTextureCoord;
out vec2 texCoord;
uniform float type;
uniform float teleso;
uniform float posX, posY, posZ;
out vec2 pos;
uniform float pi = 3.14159265359;
uniform float mode;

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
	float az = vec.x * pi;
	float ze = vec.y * pi/2;
	float r = 1;

	float x = r * cos(az) * cos(ze);
	float y =  2*  r * sin(az) * cos(ze);
	float z =       r * sin(ze);
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

vec3 getCylinder(vec2 vec){
	float s = pi*vec.x;
	float t = vec.y;
	float r = 1;
	float theta =  s;
	float z = t;
	return vec3(r*cos(theta), r*sin(theta), z);
}
vec3 getSombrero(vec2 vec) {
	float s = 3.14 * 0.75 * vec.x *2;
	float t = 3.14 *2 * vec.y;

	return vec3(
	t*cos(s),
	t*sin(s),
	sin(t))/2;
}


vec3 getPlane(vec2 vec){
	return vec3(vec*4,-1);
}
vec3 getMovingPlane(vec2 vec){
	return vec3(vec,getZ(vec));
}


vec3 getSphereNormal(vec2 vec){
	vec3 u = getSphere(vec + vec2(0.001,0))-getSphere(vec - vec2(0.001,0));
	vec3 v = getSphere(vec + vec2(0,0.001))-getSphere(vec - vec2(0,0.001));
	return cross(u,v);
}

vec3 getCylinderNormal(vec2 vec){
	vec3 u = getCylinder(vec + vec2(0.001,0))-getCylinder(vec - vec2(0.001,0));
	vec3 v = getCylinder(vec + vec2(0,0.001))-getCylinder(vec - vec2(0,0.001));
	return cross(u,v);
}

vec3 getSombreroNormal(vec2 vec){
	vec3 u = getSombrero(vec + vec2(0.001,0))-getSombrero(vec - vec2(0.001,0));
	vec3 v = getSombrero(vec + vec2(0,0.001))-getSombrero(vec - vec2(0,0.001));
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
vec3 getParsurNormal(vec2 vec){
	vec3 u = getParsur(vec + vec2(0.001,0))-getParsur(vec - vec2(0.001,0));
	vec3 v = getParsur(vec + vec2(0,0.001))-getParsur(vec - vec2(0,0.001));
	return cross(u,v);
}

vec3 getPlaneNormal(vec2 vec){
	vec3 u = getPlane(vec + vec2(0.001,0))-getPlane(vec - vec2(0.001,0));
	vec3 v = getPlane(vec + vec2(0,0.001))-getPlane(vec - vec2(0,0.001));
	return cross(u,v);
}vec3 getMovingPlaneNormal(vec2 vec){
	vec3 u = getMovingPlane(vec + vec2(0.001,0))-getMovingPlane(vec - vec2(0.001,0));
	vec3 v = getMovingPlane(vec + vec2(0,0.001))-getMovingPlane(vec - vec2(0,0.001));
	return cross(u,v);
}

vec3 normalCalculation (vec2 pos){
	vec3 testNormal;
	float distance2 = pos.x*pos.x+pos.y*pos.y;
	testNormal.x = -pi*sin(sqrt(distance2))/distance2*pos.x;
	testNormal.y = -pi*sin(sqrt(distance2))/distance2*pos.y;
	testNormal.z = 1.0;
	return testNormal;
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
			pos4 =  vec4(getParsur(pos), 1.0);
			normal = mat3(view)*getParsurNormal(pos);
		}
		else if (teleso == 0.0){
		pos4 = vec4(getWeirdSphere(pos), 1.0);
		normal = mat3(view)*getWeirdSphereNormal(pos);
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
			pos4 = vec4(getSombrero(pos), 1.0);
			normal = mat3(view)*getSombreroNormal(pos);
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
	testNormala = normalCalculation(pos.xy);
	vertColor = pos4.xyz;

	if(mode==4.0f){
		//vec3 normal = normalize(mat3(view)*getWeirdSphereNormal(pos));

	//	normal = inverse(transpose(mat3(view)*getWeirdSphereNormal(pos)))*normal;
//		vec3 lightDirection = normalize(light-normal.xyz);
	//	vertColor = vec3(normal.xyz);
	}



}
