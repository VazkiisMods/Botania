#version 150
// [VanillaCopy] rendertype_entity_translucent.vsh, changes noted

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;

uniform float GameTime;
uniform float BotaniaDisfiguration;

float rand(vec2 co) {
    return (fract(sin(dot(co.xy, vec2(12.9898,78.233))) * 43758.5453) - 0.5) * 2;
}

void main() {
    // Botania: Blur Position using disfiguration
    float seed = rand(vec2(GameTime, GameTime));
    vec3 offset = BotaniaDisfiguration * vec3(
        rand(seed * Position.yz),
        rand(seed * Position.xz),
        rand(seed * Position.xy)
    );
    vec3 modifiedPos = Position + offset;
    gl_Position = ProjMat * ModelViewMat * vec4(modifiedPos, 1.0);

    vertexDistance = fog_distance(IViewRotMat * Position, FogShape);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * vec4(Normal, 0.0);
}
