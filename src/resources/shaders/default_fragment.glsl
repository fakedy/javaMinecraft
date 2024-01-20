#version 460 core
out vec4 FragColor;

in vec3 TexCoord;
in vec3 Normal;
in vec3 FragPos;
in vec4 FragPosLightSpace;

uniform sampler2DArray ourTexture;
uniform vec3 lightPos;

uniform vec3 plyPos;
uniform int fogDist;


vec3 lightDir;
vec3 normal;

float fragDist = pow((pow((plyPos.x - FragPos.x), 2) + pow((plyPos.y - FragPos.y), 2) + pow((plyPos.z - FragPos.z), 2)), 0.5); // calculates distance between camera and frag position. seems like glsl already have a dist func lol.

vec3 lightDirection = vec3(-0.5f, -1.0f, -0.5f);

float get_fog_factor() {

    float nearplane = 0; // Where the fog starts
    float farplane =  fogDist; // where the max fog is reached

    float fogmax = 1.0 * farplane;
    float fogmin = 0.5 * farplane;
    if (fragDist >= fogmax)discard; // fog edge
    //if (fragDist <= fogmin)return 0.0; // fog edge
    fragDist = clamp(fragDist, fogmin, fogmax);

    //return 0.0;
    return 1.0 - (fogmax - fragDist) / (fogmax - fogmin); // everything between the fog edges
}


void main()
{
    float fogFactor = get_fog_factor();
    vec4 color = texture(ourTexture,TexCoord);
    normal = normalize(Normal);
    vec3 lightColor = vec3(0.6, 0.6, 0.6);
    vec3 fogColor = vec3(0.6, 0.6, 0.6);
    // ambient
    vec3 ambient = 0.1 * lightColor; // indirect light
    // diffuse
    lightDir = normalize(lightPos - FragPos);
    float diff = max(dot(lightDir, normal), 0.0);
    vec3 diffuse = diff * lightColor;
    // specular
    vec3 viewDir = normalize(plyPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, normal);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = 0.0;
    float shininess = 256.0;
    //spec = pow(max(dot(normal, halfwayDir), 0.0), shininess);
    vec3 specular = spec * lightColor;
    // calculate shadow
    vec4 lighting = vec4(ambient +    (1.0)* (diffuse + specular),1.0) * color;
    // gamma correction
    //float gamma = 2.2;
    //lighting.rgb = pow(lighting, vec3(1.0/gamma));
    FragColor = mix(vec4(lighting), vec4(fogColor, 0.0), fogFactor);
}