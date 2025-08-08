import { z, defineCollection } from 'astro:content';

const docs = defineCollection({
  type: 'content',
  schema: z.object({
    title: z.string(),
    description: z.string().optional(),
    order: z.number().default(0),
    tags: z.array(z.string()).optional(),
    updated: z.string().optional(),
  })
});

const reference = defineCollection({
  type: 'content',
  schema: z.object({
    title: z.string(),
    fqcn: z.string().optional(),
    package: z.string().optional(),
    kind: z.enum(['class', 'interface', 'enum', 'record']).optional(),
    order: z.number().default(0)
  })
});

export const collections = {
  docs,
  reference,
};
